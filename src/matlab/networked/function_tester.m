function function_tester(iter)
% load play
close all;

% load plotting data and params
eval('puppet_play');
eval('puppet_data');
eval('plot_data');

dt = 0.01;
t0 = 0;
z10 = [0.55 0.38 0]';
q0 = [0 0 0 0 0 0]';

% step sizes for tau and alpha
gt = 0.001;
ga_init = 0.0001;
ga = ga_init.*ones(1, length(MODES));

% store initial taus!
taus_init = [];
T_f = 0;
for i = 1 : length(MODES)
    taus_init = [taus_init MODES{i}.tau];
    T_f = T_f + MODES{i}.tau;
end

x10 = [z10; q0];

% assemble regions
Rho = [];
scaled_length = length_per_unit / 100;
center = scaled_length / 2;
for j = 1 : stage_dim(1) % iterate through row
    for k = 1 : stage_dim(2) % iterate in each column of that row
        rho_jk = [center+scaled_length*(k-1) center+scaled_length*(j-1)]';
        Rho = [Rho rho_jk];
    end
end

%% Cost function initialization
% Integral cost matrix
P = diag(ones(1,length(x10)));
% test
P(1,1) = 0;
P(2,2) = 0;
%
P(3,3) = 0;
P(8,8) = 0;
P(9,9) = 0;

% P = zeros(9);
% P(1,1) = 1;
% P(2,2) = 1;
% P(4,4) = 1;
% P(5,5) = 1;
% P(6,6) = 1;
% P(7,7) = 1;

% Spatial cost matrix
Q = zeros(9);
Q(1,1) = 1;
Q(2,2) = 1;

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

for idx = 1 : iter

    [X1, T, J] = forwardSimulation(dt, t0, x10, Rho, MODES, params, L_func);

    % assemble MU init conditions
    dCdAlpha = ones(1,length(MODES));
    for k = 1 : length(MODES)
        dCdAlpha(k) = alpha_penalty_factor*2*MODES{k}.alpha;
    end

    % initialize lambda with dPsi_dx
    r = zeros(9,1);
    r(1:2) = Rho(:, MODES{length(MODES)}.region);
    x1f = X1(:,length(T));
    cost_vec = zeros(1,9);
    cost_vec(1,1) = 1;
    cost_vec(1,2) = 1;
    lam0 = dPsidx(cost_vec, x1f, r);

    [L, MU, taus] = backwardSimulation(dt, lam0, Rho, dCdAlpha, X1, T, MODES);

    %% COST CALCULATION!
    rho = zeros(9,1);
    C = 0;
    Delta = 0;
    Psi = 0;
    for j = 1 : length(MODES)
        C = C + C_func(MODES{j}.alpha);
    end
    for k = 1 : length(MODES)-1
        Delta = Delta + Delta_func(taus_init(k), MODES{k}.tau);
        rho(1:2) = Rho(:,MODES{k}.region);
        Psi = Psi + Psi_func( X1(:, taus(k+1)),  rho);
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
    DJDT = dJdtau(tau_penalty_factor, T, taus, taus_init, L, X1, MODES, Rho);
    % increment taus from dJdtau
    tau_last = 0;
    for i = 1 : length(DJDT)
        MODES{i}.tau = MODES{i}.tau - gt*DJDT(i);
        tau_last = tau_last+MODES{i}.tau;
    end
    MODES{end}.tau = T_f - tau_last;

    DJDA = dJdalpha(MU);
    for i = 1 : length(MU)
%         if(norm(DJDA(i)) < 0.1)
%             ga(i) = ga(i) / 10;
%         end
        MODES{i}.alpha = MODES{i}.alpha - ga(i)*DJDA(i);
    end

    if mod(idx, 5) == 0
        disp(idx);
    end
    
    N_DT = [N_DT norm(DJDT)];
    N_DA = [N_DA norm(DJDA)];
    
%     DT = [DT DJDT'];
%     DA = [DA DJDA'];
    
end

figure(10);
plot(I, Total_Cost);
% plot(I, J_data, I, C_data, I, Delta_data, I, Psi_data);

figure(11);
subplot(2,1,1);
% plot(I, DT);
% title('dJdtau');
plot(I, N_DT);
title('|| \partial J / \partial \tau||');
subplot(2,1,2);
% plot(I, DA);
% title('dJdalpha');
plot(I, N_DA);
title('|| \partial J / \partial \alpha||');

tau_out = [];
alpha_out = [];
for j = 1 : length(MODES)
    tau_out = [tau_out MODES{j}.tau];
    alpha_out = [alpha_out MODES{j}.alpha];
end
disp('Taus: ');
disp(tau_out);
disp('Alphas: ');
disp(alpha_out);

%% END

% figure(6);
% hold on;
% for i = 1 :length(L)
%     Ti = T(1,1:length(L{i}));
%     subplot(1, length(L), i);
%     plot(Ti, L{i});
%     title(['\lambda' int2str(i)]);
% end
% hold off;
% 
% figure(7);
% hold on;
% for i = 1 : length(MU)
%     Ti = T(1,1:length(MU{i}));
%     subplot(1, length(MU), i)
%     plot(Ti, MU{i});
%     title(['\mu' int2str(i)]);
% end
% hold off;

R = evaluateLimbKinematics(X1, params);


Z = {};
Z{1} = X1(1:2,:);
% Z{2} = X2(1:2,:);

display_stage(Z,num_puppets,stage_dim);

generatePuppetGraphs(T, X1(4:9,:), R);

% makePuppetMovie(R1, 0, '/Users/pmartin/test.avi');