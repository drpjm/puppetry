function [XSOLS, J] = forward(x0, Modes, taus, alphas, Costs, R)

odeprops = odeset('RelTol', 1e-9,'AbsTol', 1e-10);

L_total = 0;
C_total = 0;
Psi_total = 0;

XSOLS = cell(1,length(Modes));

t0 = 0;

for k = 1 : length(Modes)
    
    tf = taus(k) + t0;
    ival = [t0 tf];
    
    % create current mode
    f_k = @(t,x) Modes{k}.ctrl(x, t, alphas(k), 1);
    
    % ode113
%    SOL = ode113(f_k, ival, x0, odeprops);
     SOL = ode45(f_k, ival, x0, odeprops);
    I = length(SOL.x);
    xf = SOL.y(:,I);

    % assign last value as next x0
    xf(length(xf)) = 0;
    x0  = xf;
    t0 = tf;
    
    % Cost calculation
    ref_k = zeros(9,1);
    ref_k(1:2) = R(:, Modes{k}.region);
    L_k = @(x) Costs.L_func(x, ref_k);
    T = SOL.x;
    L = zeros(1,length(T));
    for j = 1 : length(T)
        L(j) = L_k(SOL.y(1:9,j));        
    end
    L_total = L_total + trapz(T,L);
    
    % alpha cost
    C_total  = C_total + Costs.C_func(alphas(k));    
    % spatial cost
    Psi_total = Psi_total + Costs.Psi_func(SOL.y(1:9,end), ref_k);

    
    XSOLS{k} = SOL;

end

% Tau cost
D_total = 0;
for n = 1 : length(taus)-1
    tau_nom = Modes{n}.tau;
    D_total = D_total + Costs.D_func(taus(n), tau_nom);
end

J = L_total + C_total + Psi_total + D_total;
