function ret = dJdtau(M_SOLS, C_SOLS, Modes, taus, taus_nom, alphas, Rho, Costs)
    
    DJDT = zeros(1,length(Modes)-1);
    for k = 1 : length(Modes)-1
        
        tau_k_eval = sum(taus(1:k));
        x_tau_k = deval(M_SOLS{k}, tau_k_eval);
        ref_k = zeros(9,1);
        ref_k(1:2) = Rho(:,Modes{k}.region);
        
        lam_minus = deval(C_SOLS{k}, tau_k_eval);
        lam_plus = deval(C_SOLS{k+1}, tau_k_eval);

        dD = Costs.dD_func(taus(k), taus_nom(k));
        dPsi = Costs.dPsi_func(x_tau_k(1:9), ref_k);
        
        f_k = Modes{k}.ctrl(x_tau_k, tau_k_eval, alphas(k), 1);
        f_kp1 = Modes{k+1}.ctrl(x_tau_k, tau_k_eval, alphas(k+1), 1);
        
        % apply dJdtau equation
        dJdtau = lam_minus(1:9)'*f_k(1:9) - lam_plus(1:9)'*f_kp1(1:9) ...
                    + dD + dPsi*f_kp1(1:9);
        DJDT(k) = dJdtau;
        
    end

    ret = DJDT;

end