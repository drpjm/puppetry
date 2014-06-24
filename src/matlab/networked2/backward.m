function CSOLS = backward(cs_f, XSOLS, Modes, taus, alphas, R, Costs)
%
% cs_f - initialization for costate (lambda AND mu)
% XSOLS - cell array of solution structs for x
% Modes - cell array puppet play modes
% taus - array of switch times
% alphas - array of scaling params
% R - array of regions
% Costs - struct of cost functions and derviatives

odeprops = odeset('RelTol', 1e-9,'AbsTol', 1e-10);

CSOLS = cell(1,length(Modes));

t_f = sum(taus);
num_tau = length(taus);
tau_idx = num_tau;

ref_k = zeros(9,1);

for k = length(Modes) : -1 : 1
%     disp(k);
    
    mode_k = Modes{k};
    ref_k(1:2) = R(:,Modes{k}.region);
    curr_v = mode_k.v;
    time_dyn = -1;
           
    t0_x = XSOLS{k}.x(1);
    tf_x = XSOLS{k}.x(length(XSOLS{k}.x));
    % temporal boundaries
    t_f = tf_x;
    t_0 = t0_x;
    ival = [t_f t_0];
    
    % select solution for kth mode
    x_func = @(t) deval(XSOLS{k}, t);
    dfda_k = @(t,x) mode_k.d_ctrl(x, t, alphas(k), time_dyn);
    % specify dfdx with alpha, v params
    dfdx_k = @(t,x) dfdx(x, t, alphas(k), curr_v);
    % grab dLdx from Costs; embed the reference x
    dLdx_k = @(x) Costs.dL_func(x, ref_k);
    
    % costate diff eq declaration - reverse time
    cs_k = @(t, cs) costate(t, cs, t_0, x_func, dLdx_k, dfdx_k, dfda_k);
    
    % reverse integration
    SOL_K = ode113(cs_k, ival, cs_f, odeprops);
%     SOL_K = ode45(cs_k, ival, cs_f, odeprops);
    CSOLS{k} = SOL_K;
    
    % costate re-intialization
    % 1 - lambda
    lam_tau_plus = deval(SOL_K, t_0);
    x_k = x_func(t_0);
    lam_tau_minus = lam_tau_plus(1:9) + Costs.dPsi_func(x_k(1:9), ref_k)';
    % 2 - mu TODO: CHECK if it is k or k-1
    if k > 1
        mu_tau_minus = Costs.dC_func(alphas(k-1));    
    else
        mu_tau_minus = Costs.dC_func(alphas(k));    % doesn't really matter...
    end
    % 3 - time
%     t_f = t_0;
%     tau_idx = tau_idx - 1;
    cs_f = [lam_tau_minus; mu_tau_minus];

end

end
