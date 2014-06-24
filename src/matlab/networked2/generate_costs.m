function COSTS = generate_costs(P, Q, ap, tp)
% Function that creates a cost structure that contains
% the raw cost functions and their derivatives

COSTS.L_func = @(x,r)       (x - r)'*P*(x-r);
COSTS.dL_func = @(x,r)      2.*(x - r)'*P;

COSTS.Psi_func = @(x,r)     (x - r)'*Q*(x-r);
COSTS.dPsi_func = @(x,r)    2.*(x - r)'*Q;

% alpha usage costs
COSTS.C_func = @(a)     ap*a^2;
COSTS.dC_func = @(a)    2*ap*a;
% tau deviation costs
COSTS.D_func = @(t, t_nom)   tp*(t_nom - t)^2;
COSTS.dD_func = @(t, t_nom) 2*tp*(t_nom - t);
