function [Q, J, T, R] = forwardSimulation(dt, init_t, x0, modes, taus, alphas)
%
% Function that encapsulates the forward integration of the puppet's system
% dynamics.
% input:
%   init_t - starting time
%   final_t - terminating time
%   x0 - intial conditions
%   modes - MDL modes
%   taus - current switch times
%   alphas - current scaling factors
% returns:
%   Q - joint angles
%   T - time axis
%   R - cell array of the position vectors
%   J - total cost

[T_final, num_modes, tauIndices] = analyzeModes(modes, taus, dt);

T = 0:dt:T_final;
time_elapsed = 0;
t = init_t; 
i = 1;

%Integrate using Euler Approx.
Q = [];
q = x0;
Q = [Q q];
U = [];
U = [U [0 0 0 0 0 0]'];
J = 0;

shoulder_width = 0.07;
hip_width = 0.06;
r_init_right = puppetHandPos(0,shoulder_width,0,0);
r_init_left = puppetHandPos(1,shoulder_width,0,0);
rleg_init_right = puppetKneePos(0,hip_width,0);
rleg_init_left = puppetKneePos(1,hip_width,0);

mode_index = 1;
switch_index = tauIndices(mode_index);

R = cell(1,4);
R_right = [];
R_right = [R_right r_init_right];
R_left = [];
R_left = [R_left r_init_left];
R_right_leg = [];
R_right_leg = [R_right_leg rleg_init_right];
R_left_leg = [];
R_left_leg = [R_left_leg rleg_init_left];


while(i < length(T))
    
    if(i <= switch_index)
         currMode = modes{mode_index};
    else
        time_elapsed = time_elapsed + taus(mode_index);
        mode_index = mode_index + 1;
        switch_index = tauIndices(mode_index);
        currMode = modes{mode_index};
    end
    u = currMode(alphas(mode_index), t-time_elapsed, q);
    q_next = q + dt.*puppetSystem(u);
    Q = [Q, q_next];
    U = [U, u];
    t=t+dt;
    i=i+1;
    q = q_next;    
    
    % evaluate arm kinematics
    r_right = puppetHandPos(0,shoulder_width,q(1),q(2));
    R_right = [R_right r_right];
    r_left = puppetHandPos(1,shoulder_width,q(3),q(4));
    R_left = [R_left r_left];

    % evaluate leg kinematics
    rleg_right = puppetKneePos(0,hip_width,q(5));
    R_right_leg = [R_right_leg rleg_right];
    rleg_left = puppetKneePos(1,hip_width,q(6));
    R_left_leg = [R_left_leg rleg_left];

    %integral cost calculation
    J = J + dt*Lfunc(q,t);
end

R = {R_right R_left R_right_leg R_left_leg};
