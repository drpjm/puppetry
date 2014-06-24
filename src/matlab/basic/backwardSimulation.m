function [L, tauIndices, MU] = backwardSimulation(dt, lam0, dC, Q, T, modes, dmodes, taus, alphas)
%
% Function that performs the backward simulation for the puppet switch time
% optimization.
% 
%   dt - integration time step
%   init_t - initial time
%   lam0 - initialized lambda
%   Q - forward state
%   T - time array from forward sim
%   modes - system modes
%   taus - switch times
%   alphas - mode scaling

%initialize time and indices
t = T(length(T));

% grab the next to last tau index, since last index is not a real switch.
mode_idx = length(modes);
curr_switch = taus(mode_idx - 1);
% index to determine switch and termination time
k = length(T);

tauIndices = [];

% costate array
L = [];
L = [lam0' L];    % insert arrays from left
l_curr = lam0;

MU = {};
% allocate mu arrays
for i = 1 : length(modes)
    MU{i} = [];
end
MU{mode_idx} = [dC(mode_idx) MU{mode_idx}];
mu_curr = dC(mode_idx);

end_idx = 1;
switch_idx = round(curr_switch / dt) + 1;

%TODO: extend to allow for more switches
elapsed_time = T(switch_idx);
%
while(k > end_idx)

%     if(k == switch_idx + 1)
%         disp(k);
%     end
    % BACKWARD SIMULATION REQUIRES NEGATIVE dt!!!    
    if(k > switch_idx)
        % LAMBDA
        l_next = l_curr - dt.*costateSystem(Q(:,k),l_curr,modes{mode_idx});        
        % MU
        f = dmodes{mode_idx}(alphas(mode_idx), t-elapsed_time, Q(:,k));
        mu_next = mu_curr -dt.*muSystem(Q(:,k), l_curr, mu_curr, f);
        
        L = [l_next' L];
        MU{mode_idx} = [mu_next MU{mode_idx}];

        %     t = t - dt;
        l_curr = l_next;
        mu_curr = mu_next;
        k = k-1;
        t = t-dt;

    else
        mode_idx = mode_idx - 1;
        % store tau and lambda AT that tau
        tauIndices = [k tauIndices];

        % setup next MU cell
        MU{mode_idx} = [dC(mode_idx) MU{mode_idx}];
        mu_curr = dC(mode_idx);
        
        % adjust all other book keeping variables
        if(mode_idx > 0)
            if(mode_idx == 1)
                curr_switch = 0;
                elapsed_time = T(1);
            else
                curr_switch = taus(mode_idx - 1);
            end
        end
        switch_idx = round(curr_switch / dt);
    end

    
    
end