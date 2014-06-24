function [gammas, eps_break]= armijo(g0, dJdt, dJda, J_curr, x0, Modes, taus, alphas, Costs, Rho)

eps_break = 0;
gammas = g0;

num_tau = length(taus)-1;
tf = sum(taus);
a = 0.5;

% test shoot: tau and alpha
taus_tmp = taus(1:num_tau) - gammas(1)*dJdt;
taus_tmp(num_tau+1) = tf - sum(taus_tmp(1:num_tau));
alphas_tmp = alphas - gammas(2)*dJda;
[SOLS_test, J_test] = forward(x0, Modes, taus_tmp, alphas_tmp, Costs, Rho);

diff = J_test - J_curr;
armijo_val_tau = -a*gammas(1)*norm(dJdt)^2;
armijo_val_alpha = -a*gammas(2)*norm(dJda)^2;

while diff > armijo_val_tau || diff > armijo_val_alpha
    
    if diff > armijo_val_tau
        gammas(1) = gammas(1) / 2;
    end
    armijo_val_tau = -a*gammas(1)*norm(dJdt)^2;
    
    if diff > armijo_val_alpha
        gammas(2) = gammas(2) / 2;
    end
    armijo_val_alpha = -a*gammas(2)*norm(dJda)^2;
    
    if  gammas(1) < eps || gammas(2) < eps
        eps_break = 1;
        break;
    end

    taus_tmp = taus(1:num_tau) - gammas(1)*dJdt;
    taus_tmp(num_tau+1) = tf - sum(taus_tmp(1:num_tau));
    alphas_tmp = alphas - gammas(2)*dJda;
    [SOLS_test, J_test] = forward(x0, Modes, taus_tmp, alphas_tmp, Costs, Rho);
    
    diff = J_test - J_curr;
end

end