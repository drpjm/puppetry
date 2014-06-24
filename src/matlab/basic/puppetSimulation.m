function ret = puppetSimulation(opt)

%
% Simulation code for basic puppet kinematics.
%
close all;

optimize = opt;

% time step
dt = 0.01;
gt = 0.002;
ga1 = 0.0005;
ga2 = 0.0001;
% MAX_ITER = 80;
MAX_ITER = 120;
% MAX_ITER = 30;

%% mode creation - modes depend on motor model

model = @motor;

waveLeftMode = @(a,t,q) waveLeft(a, t, q, model);
walkMode = @(a,t,q) walk(a, t, q, model);

modes = {waveLeftMode, walkMode};
% modes = {walkMode, waveLeftMode};

% modes used for alpha optimization
d_model = @dMotor_dAlpha;

d_waveLeftMode = @(a,t,q) waveLeft(a, t, q, d_model);
d_walkMode = @(a,t,q) walk(a, t, q, d_model);

d_modes = {d_waveLeftMode, d_walkMode};


%% switch times
taus = [4.2 6];

%% speed scaling factor
alphas = [1 1];

% BASELINE TRAJECTORY
[T_final, num_modes, tauIndices] = analyzeModes(modes, taus, dt);

% initial conditions
x0 = [0 0 0 0 0 0]';
t = 0;
% forward simulation of the puppet
[Q, J, T, R] = forwardSimulation(dt, t, x0, modes, taus, alphas);


%% SIMULATION AND OPTIMIZATION
Cost = [];
Axis = [];
DJ_Tau = [];
DJ_A1 = [];
DJ_A2 = [];
Tau = [];
A = [];

% store desired tau
initial_tau = taus(1);
tau_penalty_factor = 1;
alpha_penalty_factor = 5;

if(optimize)
    j = 1; done = 0;
    while(j <= MAX_ITER && ~done)

        [T_final, num_modes, tauIndices] = analyzeModes(modes, taus, dt);

        % initial conditions
        x0 = [0 0 0 0 0 0]';
        t0 = 0;
        % forward simulation of the puppet
        [Q, J, T, R] = forwardSimulation(dt, t0, x0, modes, taus, alphas);

        % backward simulation for optimization data
        lambda0 = zeros(1,6);
        % mu starting condition
        dCdAlphas = alpha_penalty_factor.*[2*alphas(1) 2*alphas(2)];
        
        [L, tau_idxs, MU] = backwardSimulation(dt, lambda0, dCdAlphas, Q, T, modes, d_modes, taus, alphas);
        tau1idx = tau_idxs(1);

        % plot MU state
        T1 = T(1,1:length(MU{1}));
        T2 = T(1,length(MU{1}):length(T));
        
        subplot(1,2,1);
        plot(T1,MU{1});
        subplot(1,2,2);
        plot(T2,MU{2});
        
        % calculate dJdtaus
        u1 = modes{1}(alphas(1),T(tau1idx),Q(:,tau1idx));
        u2 = modes{2}(alphas(2),T(2),Q(:,tau1idx));
        dJdtau1 = L(:,tau1idx)'*(puppetSystem(u1)-puppetSystem(u2)) + tau_penalty_factor*2*(initial_tau - taus(1));
        norm_dJ = norm(dJdtau1);
        
        new_tau = taus(1) - gt*dJdtau1;
        if(new_tau < 0)
            done = 1;
        else
            taus(1) = new_tau;
        end
        taus(num_modes) = T_final - taus(1);
        disp(j);

        % calculate dJdalphas
        dJdalpha1 = MU{1}(1);
        dJdalpha2 = MU{2}(1);
        new_a1 = alphas(1) - ga1*dJdalpha1;
        new_a2 = alphas(2) - ga2*dJdalpha2;
        
%         % TODO: Armijo step size
%         forward = @(a) forwardSimulation(dt, t0, x0, modes, taus, a);
%         % tuning
%         alfa = 0.5;
%         beta = 0.5;
%         h = [dJdalpha1 dJdalpha2];
%         dJ = [dJdalpha1 dJdalpha2];
%         % constrain h!
%         new_alphas = armijo_stepsize(forward, T_final, J, alphas, h, dJ, alfa, beta);
%         new_a1 = new_alphas(1);
%         new_a2 = new_alphas(2);
%         
%         alphas(1) = new_a1;
%         alphas(2) = new_a2;
        
        % filter out values out of feasible set
        if(new_a1 > 2)
            alphas(1) = 2;
        else
            alphas(1) = new_a1;
        end
        if(new_a2 > 2)
            alphas(2) = 2;
        else
            alphas(2) = new_a2;
        end
        
        Axis = [Axis j];
        
        % add in tau costs
        D = tau_penalty_factor*(initial_tau - taus(1))^2;
        C1 = alpha_penalty_factor*alphas(1)^2;
        C2 = alpha_penalty_factor*alphas(2)^2;
        J = J + D + C1 + C2;
        Cost = [Cost J];
        
        DJ_Tau = [DJ_Tau norm_dJ];
        Tau = [Tau taus(1)];
        
        DJ_A1 = [DJ_A1 norm(dJdalpha1)];
        DJ_A2 = [DJ_A2 norm(dJdalpha2)];
        
        A = [A alphas'];
        
        % TERMINATION condition
        norm_dJ = norm([dJdtau1 dJdalpha1 dJdalpha2]);
        if(norm_dJ < 3)
            done = 1;
        end
        
        if(mod(j,10) == 0)
            disp(norm_dJ);
%             figure(9);
            %     subplot(3,1,1);
%             plot(Axis, Cost);
%             title('Total Cost (J) vs. Iteration');
%             ylabel('J (dimensionless)');
%             xlabel('Iteration Index');
        end
        
        j = j+1;
    end
end

if(optimize)
    taus
    alphas
    dJdtau1
    dJdalpha1
    dJdalpha2
end

%% Graphics

% generatePuppetGraphs(T,Q,R);

final_cost = J

% makePuppetMovie(R, 1,'/Users/pmartin/Documents/GT/research/Media/Puppets/puppetMovie_nonopt.avi');

if(optimize)
    % Costate graph
%     figure;
%     subplot(2,1,1);
%     plot(T,L(1,:), T,L(2,:), T, L(5,:));
%     legend('\lambda_{1}','\lambda_{2}', '\lambda_{5}');
%     title('Costate Trajectory - Right Arm');
%     subplot(2,1,2);
%     plot(T,L(3,:), T,L(4,:), T, L(6,:));
%     legend('\lambda_{3}','\lambda_{4}', '\lambda_{6}');
%     title('Costate Trajectory - Left Arm');

%     figure;
%     subplot(3,1,1);
%     plot(Axis, Cost);
%     title('Total Cost (J) vs. Iteration');
%     subplot(3,1,2);
%     plot(Axis, DJ_Tau);
%     title('dJ/d\tau vs. Iteration');
%     subplot(3,1,3);
%     plot(Axis, Tau);
%     title('Switch Time (\tau) vs. Iteration');
    
%     figure;
%     subplot(3,1,1);
%     plot(Axis, DJ_A1);
%     title('dJd\alpha_{1} vs. Iteration');
%     subplot(3,1,2);
%     plot(Axis, DJ_A2);
%     title('dJd\alpha_{2} vs. Iteration');
%     subplot(3,1,3);
%     plot(Axis, A);
%     title('Scaling Factors (\alpha_{1}, \alpha_{2}) vs. Iteration');

%% RSS08 Graphics
    figure;
%     subplot(3,1,1);
    plot(Axis, Cost);
    title('Total Cost (J) vs. Iteration');
    ylabel('J (dimensionless)');
    xlabel('Iteration Index');

    figure;
    subplot(2,1,1);
    plot(Axis, DJ_Tau);
    title('dJd\tau vs. Iteration');
    xlabel('Iteration Index');
    subplot(2,1,2);
    plot(Axis, DJ_A1, Axis, DJ_A2);
    title('dJd\alpha vs. Iteration');
    xlabel('Iteration Index');


end
