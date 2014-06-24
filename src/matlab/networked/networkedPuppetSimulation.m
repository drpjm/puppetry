function networkedPuppetSimulation()
%   Simulation for performing networked puppet switched time optimization.
%
close all;

eval('puppet_play');
eval('puppet_data');
eval('plot_data');

MAX_ITER = 110;

dt = 0.01;
t0 = 0;

%% Initial Conditions for puppets
z01 = [0.55 0.30 0]';
q01 = [0 0 0 0 0 0]';
z02 = [0.40 1.40 0]';
q02 = [0 0 0 0 0 0]';
x0 = [[z01; q01] [z02; q02]];

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
P = diag(ones(1,length(x0)));
% test
P(1,1) = 0;
P(2,2) = 0;
%
P(3,3) = 0;
P(8,8) = 0;
P(9,9) = 0;
% Spatial cost matrix
Q = zeros(9);
Q(1,1) = 1;
Q(2,2) = 1;
% handles to costs
L_func = @(x, r)        (x - r)'*P*(x - r);
Psi_func = @(x, r)      (x - r)'*Q*(x - r);
Delta_func = @(t, t_i, tp)       tp*(t_i - t)^2;
C_func = @(a, ap)        ap*a^2;

% store initial taus and end times for puppets
tau0 = [];
tf = [];
for i = 1 : length(PLAY)
    Curr_Modes = PLAY{i};
    curr_tau0 = [];
    tf_curr = 0;
    for j = 1 : length(Curr_Modes)
        curr_tau0 = [curr_tau0 Curr_Modes{j}.tau];
        tf_curr = tf_curr + Curr_Modes{j}.tau;
    end
    tau0 = [tau0 curr_tau0'];
    tf = [tf tf_curr];
end
% tau constraint: tau_j <= tau_k
% p_j = [2 2];    % puppet 2, tau_2
% p_k = [1 2];    % puppet 1, tau_2
p_j = [2 1];    % puppet 2, tau_1
p_k = [1 1];    % puppet 1, tau_1
constraint = [p_j' p_k'];
nu0 = extractConstraint(constraint, PLAY);
Nu = [];
nu = nu0;

%% Main algorithm iteration
num_puppets = length(PLAY);
Z = {};
I = [];
CurrCost = [];
TotalCost = [];
I = [];
N_DT = [];
N_DA = [];
% DT = [];
% DA = [];
% extra debug variables
C_data = [];
Delta_data = [];
Psi_data = [];
J_data = [];
TJ = [];
TK = [];

done = zeros(1,num_puppets);
idx = 1;
% for idx = 1 : MAX_ITER
while (idx <= MAX_ITER && length(find(done)) < num_puppets)

    Nu = [Nu nu];
    
    %% Iterate through all puppets
    for j = 1 : num_puppets

        Curr_Modes = PLAY{j};
        % main puppet simulation
        [ T, X, L, MU, taus, J ] = puppetSimulation(t0, x0(:,j), Curr_Modes, Rho, P, dt, j);

        % COST CALCULATION for puppet j
        rho = zeros(9,1);
        C = 0;
        Delta = 0;
        Psi = 0;
        for m = 1 : length(Curr_Modes)
            C = C + C_func(Curr_Modes{m}.alpha, alpha_penalties(j));
        end
        for k = 1 : length(Curr_Modes)-1
            Delta = Delta + Delta_func(tau0(k), Curr_Modes{k}.tau, tau_penalties(j));
            rho(1:2) = Rho(:,Curr_Modes{k}.region);
            Psi = Psi + Psi_func( X(:, taus(k+1)),  rho);
        end
        TotalCost_j = J + C + Delta + Psi;
        CurrCost(j) = TotalCost_j;
        
        % descent for tau and alpha
        % step sizes for tau and alpha
        gt = 0.001;
        gn = gt;
        ga_init = 0.0001;
        ga = ga_init.*ones(1, length(Curr_Modes));

        %  TAU FONC
        DJDT = dJdtau(tau_penalties(j), T, taus, tau0(:, j), L, X, Curr_Modes, Rho);
%         DJDT = dJdtau(tau_penalty_factor, T, taus, tau0(:, j), L, X, Curr_Modes, Rho);

        % increment taus from dJdtau
        tau_last = 0;
        for n = 1 : length(DJDT)
            % new networked code:
            if( p_j(1) == j && p_j(2) == n)
                TJ = [TJ Curr_Modes{n}.tau];
                step = DJDT(n) + nu;
            elseif   (p_k(1) == j && p_k(2) == n)
                TK = [TK Curr_Modes{n}.tau];
                step = DJDT(n) - nu;
            else
                step = DJDT(n);
            end
%                 step = DJDT(n);

            Curr_Modes{n}.tau = Curr_Modes{n}.tau - gt*step;
            tau_last = tau_last+Curr_Modes{n}.tau;
        end
        Curr_Modes{end}.tau = tf(j) - tau_last;
        N_DT(j, idx) = norm(DJDT);
        
        %test for termination condition
        if(N_DT(j, idx) < 3 && done(j) == 0)
            done(j) = 1;
        end
        num_done = length(find(done));
        
        % ALPHA FONC
        DJDA = dJdalpha(MU);
        for n = 1 : length(MU)
            %         if(norm(DJDA(i)) < 0.1)
            %             ga(i) = ga(i) / 10;
            %         end
            Curr_Modes{n}.alpha = Curr_Modes{n}.alpha - ga(n)*DJDA(n);
        end
        
        PLAY{j} = Curr_Modes;
        Z{j} = X(1:2,:);

    end
    nu = nu + gn*(extractConstraint(constraint, PLAY));
    
    % end puppet iteration
    TotalCost = [TotalCost CurrCost'];
    I = [I idx];
    if(mod(idx, 5) == 0)
        disp(idx);
    end    

    idx = idx + 1;
    
    
end
% end main iteration
figure(1);
plot(I, TotalCost);
title('Puppet Costs (J)');
legend('Puppet 1', 'Puppet 2');
figure(2);
plot(I, Nu);
title('\nu Costate');
figure(3);
plot(I, TJ, I, TK);
title('\tau_j <= \tau_k');
legend('\tau_j', '\tau_k');
xlabel('Iteration');
ylabel('Time (s)');

TJ(length(TJ))
TK(length(TK))

figure(4);
plot(I, N_DT);
title('\partial J / \partial\tau vs. Iteration');
legend('Puppet 1','Puppet 2');

PLAY
%% Plot all puppets on stage
display_stage(Z, num_puppets, stage_dim);


%% Query for puppet traj to display

% makePuppetMovie(R1, 0, '/Users/pmartin/test.avi');