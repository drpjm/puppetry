function ret = dJdtau(penalty_factor, T, taus, taus_init, L, X, Modes, Rho)
% Function that returns the array of dJdtau FONC.
%
% T - time axis
% taus - array of switches
% taus_init - array of initial tau switch times
% L - lambda costate
% X - state trajectory
% Modes - puppet play object

i = length(taus);
ret = [];
tf = T(end);
while i > 1
    
    r = zeros(9,1);
    r(1:2) = Rho(:, Modes{i-1}.region);
    
    % time shifting
    elapsed_time_curr = T(taus(i));
    if (taus(i - 1)) == 0
        elapsed_time_prev = 0;
    else
        elapsed_time_prev = T(taus(i-1));
    end
    
    l_minus = L{i-1}(:,length(L{i-1}));
    l_plus = L{i}(:,1);
    
    % calculate mode difference
    curr_mode = Modes{i}.ctrl;
    prev_mode = Modes{i-1}.ctrl;
    x_tau = X(:, taus(i));
    curr_t = T(taus(i))-elapsed_time_curr;
    prev_t = T(taus(i))-elapsed_time_prev;
    curr_f = curr_mode(x_tau, curr_t, Modes{i}.alpha);
    prev_f = prev_mode(x_tau, prev_t, Modes{i-1}.alpha);
    
    diff = l_minus'*prev_f - l_plus'*curr_f;
    dDdtau = 2*penalty_factor*(taus_init(i-1) - Modes{i-1}.tau);
    dPsiTerm = dPsidx(ones(1,9), x_tau, r)'*curr_f;
    
    % dJdtau derivative!
    dJ = diff + dDdtau + dPsiTerm;
    
    ret =  [dJ ret];
    
    i = i - 1;
end
