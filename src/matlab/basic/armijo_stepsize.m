function [u_n] = armijo_stepsize(system, T, J, u, h, dJ, alpha, beta)
% Implements the Armijo step size function.
%
% system - function link
% T - final time for simulation
% u - control parameters to optimize
%
% h - projection
% dJ - grad J
% alpha - Armijo tuning param 1
% beta - Armijo tuning param 2
%
% u_n - Returns the new control parameters

k = 0;
u_n = u - (beta^k)*h;
[X, J_new] = system(u_n);

product =dJ*h';
diff = J_new - J;
while(diff > -alpha*(beta^k)*product && (beta^k) > eps)
    k = k + 1;
    u_n = u - (beta^k)*h;
    [X, J_new] = system(u_n);
    diff = J_new - J;
end