function [M, T] = muSimulation(dt, init_t, dC, X, L, T, modes, tau_indices, alphas)
%
% Function that simulates the 2nd costate system for alpha optimization.
% Returns the costate values and time axis.
%
%   dt - integration time step
%   init_t - initial time
%   lam0 - initialized lambda
%   X = state
%   L -  costate
%   T - time array from forward sim
%   modes - system modes
%   taus - switch times
%   alphas - mode scaling


% Modes in this part of the simulation are the d_f/d_alphas terms.
M = [];
m_curr = dC(1);
M = [M m_curr];

%% Initialize
t = init_t;
mode_idx = 1;
switch_idx = tau_indices(1);
time_elapsed = 0;
% time index
k = 1;

while(k < length(T))
    
    % mode selection
    if(k <= switch_idx)
         currMode = modes{mode_idx};
    else
        time_elapsed = time_elapsed + (tau_indices(mode_idx)-1)*dt;
        mode_idx = mode_idx + 1;
        if(mode_idx > length(tau_indices))
            switch_idx = length(T);
        else
            switch_idx = tau_indices(mode_idx);
        end
        currMode = modes{mode_idx};
    end
    
    % evaluate system
    q = X(:,k);
    l = L(:,k)';
    u = currMode(alphas(mode_idx), t-time_elapsed, q);
    m_next = m_curr + dt.*musystem(q,l,m_curr,u);
    M = [M m_next];
    
%     if(k == length(T)-1)
%         disp(k);
%     end
    
    k = k+1;
end