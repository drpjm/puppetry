function [X, T, J] = forwardSimulation(dt, t0, x0, Rho, Modes, puppet_params, cost_func)
%
% Function that encapsulates the forward integration of the puppet's system
% dynamics.
% input:
%   t0 - starting time
%   x0 - intial conditions
%   Modes - cell of MDLp mode STRUCTS
%
% returns:
%   X - puppet state
%   T - time axis
%   J - total cost

odeprops = odeset('RelTol', 1e-6,'AbsTol', 1e-9);

%% time indexing

% extract tau indices and final time
% tf = t0;
% tau_indices = [];
% for i = 1 : length(Modes)
%     curr_tau = Modes{i}.tau;
%     tau_indices = [tau_indices round( (curr_tau+tf) / dt ) ];
%     tf = tf + curr_tau;
% end

% T = t0:dt:tf;
% t = t0;
% i = 1;

% mode_index = 1;
% switch_index = tau_indices(mode_index);

t0 = 0;
tf = 0;

% solution data structure
XSOL = cell(1,length(Modes));

for n = 1 : length(Modes)
    
    tf = Modes{n}.tau + t0;
    ival = [t0 tf];
    
    % call ode on current mode function on interval
    curr_mode = Modes{n}.ctrl;
    
    XSOL{n} = ode113(curr_mode, ival, x0, odeprops);
    
    t0 = tf;
end


%% State init
% [n,m] = size(x0);
% X = zeros(n,length(T));
% X(:,1) = x0;
% x = x0;
% X = [X x];
% J = 0;

% curr_mode = Modes{mode_index}.ctrl;
% curr_alpha = Modes{mode_index}.alpha;
% curr_P = Modes{mode_index}.P;
% curr_r = zeros(9,1);
% curr_r(1:2) = Rho(:, Modes{mode_index}.region);

% handle to L function
% cost_func = @(x, r,P)        (x - r)'*P*(x - r);
% 
% time_elapsed = time_elapsed + Modes{mode_index}.tau;
% mode_index = mode_index + 1;
% switch_index = tau_indices(mode_index);
% curr_mode = Modes{mode_index}.ctrl;
% curr_alpha = Modes{mode_index}.alpha;
% curr_r(1:2) = Rho(:,Modes{mode_index}.region);

%% OLD EULER!
%     system_step = curr_mode(x, t-time_elapsed, curr_alpha);
%     x = X(:,i);
%     system_step = curr_mode(X(:,i), t-time_elapsed, curr_alpha);

%     X(:,i+1) = X(:,i) + dt.*system_step;

%     i=i+1;
%     t=t+dt;

%integral cost calculation
%      J = J + dt*cost_func(x, curr_r, curr_P);
%     L = cost_func(X(:,i), curr_r);
%     J = J + dt*L;
% end
