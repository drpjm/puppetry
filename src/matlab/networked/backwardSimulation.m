function [L, MU, tau_indices] = backwardSimulation(dt, lam0, Rho, dC, X, T, Modes)
%
% Function that performs the backward simulation for the puppet switch time
% optimization.
% 
%   dt - integration time step
%   lam0 - initialized lambda
%   Rho -  region centroid matrix
%   dC - array of dC_dAlpha values
%   X - forward state
%   T - time array from forward sim
%   Modes - system modes

%% initialize time and indices
T_length = length(T);
t = T(T_length);
tf = t;
tau_indices = [];
for i = length(Modes): -1 : 1
    curr_tau = Modes{i}.tau;
    tau_indices = [round( (tf-curr_tau) / dt ) tau_indices];
    tf = tf - curr_tau;
end

% grab the next to last tau index, since last index is not a real switch.
mode_index = length(Modes);
switch_index = tau_indices(mode_index);

% costate array
L = {};
% L = [lam0 L];    % insert arrays from left
for i = 1 : length(Modes)
    L{i} = [];
end
l_curr = lam0;

MU = {};
% allocate mu arrays
for i = 1 : length(Modes)
    MU{i} = [];
end
mu_curr = dC(mode_index);


elapsed_time = T(switch_index);

% reference for L
ref = zeros(9,1);

k = T_length;
while(k >= 1)
    
%     if(k == switch_index + 1)
%         disp(k);
%     end
    
    if(k > switch_index)
        
        % LAMBDA
        curr_alpha = Modes{mode_index}.alpha;
        curr_v = Modes{mode_index}.v;
        curr_x = X(:,k);
        
        % index into region centroid array; put coordinates in ref array
        ref(1:2) = Rho(:, Modes{mode_index}.region);
        
        % calculate the df_dx term for lambda costate evolution
        dfdx = [ curr_alpha*curr_v*cos(curr_x(3)) -curr_alpha*curr_v*sin(curr_x(3)) ];
        
        l_next = l_curr - dt.*costateSystem(curr_x, l_curr, ref, dfdx);

        % MU
        f_z = [ curr_v*sin(curr_x(3)); curr_v*cos(curr_x(3)); 0 ];
        dfdalpha = Modes{mode_index}.d_ctrl;
        f = dfdalpha(curr_x, t-elapsed_time, curr_alpha);
        f(1:3) = f_z;

        mu_next = mu_curr - dt.*muSystem(curr_x, l_curr, mu_curr, f);
        
        % store lambda and mu
        L{mode_index} = [l_next L{mode_index}];
        MU{mode_index} = [mu_next MU{mode_index}];
 
        l_curr = l_next;
        mu_curr = mu_next;

        k = k-1;
        t = t-dt;

    else
        mode_index = mode_index - 1;
        
        % setup next L cell
        cost_vec = zeros(1,9);
        cost_vec(1,1) = 1;
        cost_vec(1,2) = 1;
%         cost_vec  = 5.*cost_vec;
        cost_vec  = 50.*cost_vec;
        
        l_curr = l_curr + dPsidx(cost_vec, curr_x, ref);
        L{mode_index} = [l_curr L{mode_index}];
        % setup next MU cell
        MU{mode_index} = [dC(mode_index) MU{mode_index}];
        mu_curr = dC(mode_index);
        
        % adjust all other book keeping variables
        if(mode_index > 0 && mode_index ~= 1)
                switch_index = tau_indices(mode_index);
                elapsed_time = T(switch_index);
        else                
                switch_index = 0;
                elapsed_time = T(1);
        end
        
    end
    
end