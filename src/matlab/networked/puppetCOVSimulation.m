function [ X, R, Total_Cost, N_DT, N_DA, I, tau_out, alpha_out ] = puppetCOVSimulation(z0, q0, Modes, Rho,  MAX_ITER)
% Function that performs COV optimization for a multimode play for one
% puppet.
%
%   INPUT:
%   z0 - 2D init, (x, y, heading)
%   q0 - init joint configuration
%   Modes - play for puppet
%   MAX_ITER - max number of iterations before dump
%
%   OUTPUT:
%   X - state trajectory
%   R - evaluated kinematics
%   Total_Cost - array of computed cost
%   N_DT
%   N_DA
%   tau_out
%   alpha_out

% load plotting data and params
% eval('puppet_play');
eval('puppet_data');

dt = 0.01;
t0 = 0;
% z0 = [0.55 0.38 0]';
% q0 = [0 0 0 0 0 0]';

% step sizes for tau and alpha
gt = 0.001;
ga_init = 0.00001;
ga = ga_init.*ones(1, length(Modes));

% store initial taus!
taus_init = [];
T_f = 0;
for i = 1 : length(Modes)
    taus_init = [taus_init Modes{i}.tau];
    T_f = T_f + Modes{i}.tau;
end

x0 = [z0; q0];

%% Cost function initialization
% Integral cost matrix
P = diag(ones(1,length(x0)));
P(1,1) = 5;
P(2,2) = 5;
P(3,3) = 0;
P(8,8) = 0;
P(9,9) = 0;

% Spatial cost matrix
Q = zeros(9);
Q(1,1) = 1;
Q(2,2) = 1;
% Q = 5.*Q;
Q = 50.*Q;

% handles to costs
L_func = @(x, r)        (x - r)'*P*(x - r);
Psi_func = @(x, r)      (x - r)'*Q*(x - r);
Delta_func = @(t, t_i)       tau_penalty_factor*(t_i - t)^2;
C_func = @(a)        alpha_penalty_factor*a^2;


%% BASIC ITERATION!!!
Total_Cost = [];
I = [];
% extra debug variables
C_data = [];
Delta_data = [];
Psi_data = [];
J_data = [];
N_DT = [];
N_DA = [];
% DT = [];
% DA = [];
idx = 1;
done = 0;

% for idx = 1 : MAX_ITER
while (idx <= MAX_ITER && done ~= 1)
    
%     [X, T, J] = forwardSimulation(dt, t0, x0, Rho, Modes, params);
    [X, T, J] = forwardSimulation(dt, t0, x0, Rho, Modes, params,L_func);

    % assemble MU init conditions
    dCdAlpha = ones(1,length(Modes));
    for k = 1 : length(Modes)
        dCdAlpha(k) = alpha_penalty_factor*2*Modes{k}.alpha;
    end

    % initialize lambda with dPsi_dx
    r = zeros(9,1);
    r(1:2) = Rho(:, Modes{length(Modes)}.region);
    x1f = X(:,length(T));
    cost_vec = zeros(1,9);
    cost_vec(1,1) = 1;
    cost_vec(1,2) = 1;
%     cost_vec = 5.*cost_vec;
    cost_vec = 50.*cost_vec;

    lam0 = dPsidx(cost_vec, x1f, r);

    [L, MU, taus] = backwardSimulation(dt, lam0, Rho, dCdAlpha, X, T, Modes);

    %% COST CALCULATION!
    rho = zeros(9,1);
    C = 0;
    Delta = 0;
    Psi = 0;
    for j = 1 : length(Modes)
        C = C + C_func(Modes{j}.alpha);
    end
    for k = 1 : length(Modes)-1
        Delta = Delta + Delta_func(taus_init(k), Modes{k}.tau);
        rho(1:2) = Rho(:,Modes{k}.region);
        Psi = Psi + Psi_func( X(:, taus(k+1)),  rho);
    end
%     C_data = [C_data C];
%     Delta_data = [Delta_data Delta];
%     Psi_data = [Psi_data Psi];
%     J_data = [J_data J];

    New_Cost = J + C + Delta + Psi;

    Total_Cost = [Total_Cost New_Cost];
    I = [I idx];

    %% GRADIENT DESCENT!
    % calculate the tau FONC
    DJDT = dJdtau(tau_penalty_factor, T, taus, taus_init, L, X, Modes, Rho);
    % increment taus from dJdtau
    tau_last = 0;
    for i = 1 : length(DJDT)
        Modes{i}.tau = Modes{i}.tau - gt*DJDT(i);
        tau_last = tau_last+Modes{i}.tau;
    end
    Modes{end}.tau = T_f - tau_last;

    DJDA = dJdalpha(MU);
    for i = 1 : length(MU)
        Modes{i}.alpha = Modes{i}.alpha - ga(i)*DJDA(i);
    end

    if mod(idx, 5) == 0
        disp(idx);
    end
    
    N_DT = [N_DT norm(DJDT)];
    N_DA = [N_DA norm(DJDA)];
    
    if (N_DT(idx) < 2.5)
        done  = 1;
        idx
    end
        
    
    idx = idx + 1;
end

tau_out = [];
alpha_out = [];
for j = 1 : length(Modes)
    tau_out = [tau_out Modes{j}.tau];
    alpha_out = [alpha_out Modes{j}.alpha];
end
disp('Taus: ');
disp(tau_out);
disp('Alphas: ');
disp(alpha_out);

%% END

R = evaluateLimbKinematics(X, params);

% Z = {};
% Z{1} = X(1:2,:);
% Z{2} = X2(1:2,:);

% generatePuppetGraphs(T, X(4:9,:));