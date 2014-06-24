function networkedPuppetSimulation()
% NOTE: x and costate have an extra 'time' state

close all;

eval('puppet_play');
eval('puppet_data');
eval('init_cond');
eval('plot_data');

[x_n, x_m] = size(X0);
num_puppets = length(PLAY);

Taus = cell(1,num_puppets);
Alphas = cell(1,num_puppets);

N_DJDT = cell(1, num_puppets);
N_DJDA = cell(1, num_puppets);

% unravel nominal parameters
for i = 1 : num_puppets
    [ts,as] = extract_nominal(PLAY{i});
    Taus{i} = ts;
    Alphas{i} = as;
end

% tau and alpha step sizes -- initial guess
gt = 0.001;
ga = 0.0001;
gammas = [gt ga];

P = zeros(x_n-1);
P(1,1) = 5;
P(2,2) = 5;
P(4,4) = 1;
P(5,5) = 1;
P(6,6) = 1;
P(7,7) = 1;

Q = zeros(x_n-1);
Q(1,1) = 1;
Q(2,2) = 1;

Costs = generate_costs(P, Q, alpha_penalty, tau_penalty);

% region definition
Rho = [];
scaled_length = length_per_unit / 100;
center = scaled_length / 2;
for j = 1 : stage_dim(1) % iterate through row
    for k = 1 : stage_dim(2) % iterate in each column of that row
        rho_jk = [center+scaled_length*(k-1) center+scaled_length*(j-1)]';
        Rho = [Rho rho_jk];
    end
end

MAX_ITER = 20;

idx = 1;
IDX = cell(1,num_puppets);
JJ = cell(1,num_puppets);
eps_breaks = zeros(1,num_puppets);
done = 0;

Puppet_SOLS = cell(1,num_puppets);

while idx < MAX_ITER %&& eps_break ~= 1 %&& ~done
    index_str = sprintf('Iteration %i\n', idx);
    fprintf(1, index_str);

    %% Primary Simulation
    for i = 1 : num_puppets

        if eps_breaks(i) ~= 1
            IDX{i} = [IDX{i} idx];
            Modes = PLAY{i};
            num_modes = length(Modes);
            taus = Taus{i};
            alphas = Alphas{i};
            %% FORWARD -- returns numerical solutions to each mode

            [M_SOLS, J_curr] = forward(X0(1:10,i), Modes, taus, alphas, Costs, Rho);
            Puppet_SOLS{i} = M_SOLS;
            JJ{i} = [JJ{i} J_curr];
            puppet_str = sprintf('=> Puppet %i : current cost = %f\n', i, J_curr);
            fprintf(1, puppet_str);

            % Calculate costate initialization ...
            region = Modes{num_modes}.region;
            SOL_k = M_SOLS{num_modes};
            t_f = sum(taus);
            x_f = deval(SOL_k, t_f);
            % pull out time variable
            x_f = x_f(1:9);
            ref_f = zeros(9,1);
            ref_f(1:2) = Rho(:,region);
            lam_f = Costs.dPsi_func(x_f, ref_f);
            mu_f = Costs.dC_func(alphas(num_modes));

            % costate (cs) has dimension 9 + 1 (mu) + 1 (time)
            cs_f = [lam_f'; mu_f];

            %% BACKWARD
            C_SOLS = backward(cs_f, M_SOLS, Modes, taus, alphas, Rho, Costs);

            %% GRADIENT STEP -- changes taus and alphas for current puppet
            [ts_nom, as_nom] = extract_nominal(Modes);

            % alpha derivatives
            DJDA = dJda(C_SOLS, taus);
            % tau derivatives
            DJDT = dJdtau(M_SOLS, C_SOLS, Modes, taus, ts_nom, alphas, Rho, Costs);

            % calculate step size
            [gammas, eps_breaks(i)] = armijo(gammas, DJDT, DJDA, J_curr, X0(1:10,i), Modes, taus, alphas, Costs, Rho);

            % apply gradient descent!
            taus_tmp = taus(1:2) - gammas(1)*DJDT;
            taus(1:2) = taus_tmp;
            taus(length(DJDT)+1) = sum(Taus{i}) - sum(taus(1:length(DJDT)));
            Taus{i} = taus;

            alphas = alphas - gammas(2)*DJDA;
            Alphas{i} = alphas;

            N_DJDT{i} = [N_DJDT{i} norm(DJDT)];
            N_DJDA{i} = [N_DJDA{i} norm(DJDA)];

            gammas(1) = gammas(1) * 4;
            gammas(2) = gammas(2) * 4;

            if eps_breaks(i) == 1
                s = sprintf('=> Puppet %i -- EPS bound reached!\n', i);
                fprintf(s);
%                 break;
            end
        end
    end

    %% Network co-state propogation
    
    idx = idx + 1;
end

Puppets_Z = cell(1,num_puppets);
Puppets_Q = cell(1,num_puppets);
Puppets_T = cell(1,num_puppets);
for n = 1 : num_puppets
    
    % plot trajectory
%     figure(n);
    Curr_SOL = Puppet_SOLS{n};
    % append trajectories for data storage and plotting
    for m = 1 : length(Curr_SOL)
        Puppets_Z{n} = [Puppets_Z{n} Curr_SOL{m}.y(1:2,:)];
        Puppets_Q{n} = [Puppets_Q{n} Curr_SOL{m}.y(4:9,:)];
        Puppets_T{n} = [Puppets_T{n} Curr_SOL{m}.x];
%         hold on;
%         subplot(2,1,1);
%         plot(Curr_SOL{m}.x,Curr_SOL{m}.y(1:2,:));
%         title('Planar Motion');
%         hold off;
%         hold on;
%         subplot(2,1,2);
%         plot(Curr_SOL{m}.x,Curr_SOL{m}.y(4:9,:));
%         title('Joint Motion');
%         hold off;

    end
    
    %% NEEDS TO WORK FOR MULTIPLE PUPPETS
    % write joint data to files - combine time + angles
%     Right_limbs(1,:) = Puppets_T{n}(:,:);
%     Right_limbs(2:3,:) = Puppets_Q{n}(1:2,:);
%     Right_limbs(4,:) = Puppets_Q{n}(5,:);
%     Right_limbs = Right_limbs';
%     csvwrite('/Users/pmartin/Desktop/right_arm.txt', Right_limbs);
    
    
    figure(n + 10);
    subplot(3,1,1);
    plot(IDX{n}, JJ{n});
    ps1 = ['Total Cost - Puppet ' int2str(n)];
    title(ps1);
    subplot(3,1,2);
    plot(IDX{n}, N_DJDT{n});
    ps1 = ['dJd\tau - Puppet ' int2str(n)];
    title(ps1);
    subplot(3,1,3);
    plot(IDX{n}, N_DJDA{n});
    ps1 = ['dJd\alpha - Puppet ' int2str(n)];
    title(ps1);

    Taus{n}
    Alphas{n}
end
display_stage(Puppets_Z, num_puppets, stage_dim);