function multiPuppetSimulation()

%
% Simulation code for basic puppet kinematics.
%
close all;

eval('multi_puppet_play');
eval('puppet_data');
eval('plot_data');

%% Initial Conditions for puppets

z0(:,1) = [0.55 0.30 0]';
q0(:,1) = [0 0 0 0 0 0]';
z0(:,2) = [0.40 1.60 0]';
q0(:,2) = [0 0 0 0 0 0]';
z0(:,3) = [1.30 0.80 0]';
q0(:,3) = [0 0 0 0 0 0]';


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


%% Iterate through all puppets
num_puppets = length(PLAY)
Zi = {};
Zf = {};
figure_num = 1;

%% Generate initial stage picture:
for i = 1 : num_puppets
    zi0 = z0(:,i);
    qi0 = q0(:,i);
    % Simulate WITHOUT optimization
    [ X ] = puppetCOVSimulation(zi0, qi0, PLAY{i}, Rho, 1);
    Zi{i} = X(1:2,:);

end    
display_stage(Zi, num_puppets, stage_dim);

%% Optimize system!
for i = 1 : num_puppets
    zi0 = z0(:,i);
    qi0 = q0(:,i);
    
    % Calculate COV
    [ X, R, Total_Cost, N_DT, N_DA, I, tau_out, alpha_out ] = puppetCOVSimulation(zi0, qi0, PLAY{i}, Rho, 100);

%     figure(figure_num);
%     plot(I, Total_Cost);
%     title(['Puppet ' int2str(i) ' Total Cost']);
%     xlabel('Iterations');
%     % plot(I, J_data, I, C_data, I, Delta_data, I, Psi_data);
%     figure_num = figure_num + 1;

    figure(figure_num);
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
    figure_num = figure_num + 1;

    Zf{i} = X(1:2,:);
    J{i} = [Total_Cost; I];
end

figure(figure_num);
title('Total Cost vs. Iteration');
plot( J{1}(2,:), J{1}(1,:),  J{2}(2,:), J{2}(1,:), J{3}(2,:), J{3}(1,:) );
% figure(figure_num+1);
% title('Total Cost vs. Iteration');
% plot(J{3}(2,:), J{3}(1,:));

%% Plot all puppets on stage
display_stage(Zf, num_puppets, stage_dim);


%% Query for puppet traj to display

% makePuppetMovie(R1, 0, '/Users/pmartin/test.avi');