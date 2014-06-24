function simple_puppet_simulation()
% Function that simulates and plots a single puppet motion sequence.

close all;
path('/Users/grits/projects/gritspuppets/src/matlab/networked2/modes', path);
path('/Users/grits/projects/gritspuppets/src/matlab/networked2/models', path);
path('/Users/grits/projects/gritspuppets/src/matlab/networked2/graphics', path);

eval('puppet_play');
eval('puppet_data');
eval('init_cond');
eval('plot_data');

% initial conditions for all puppets
[x_n, x_m] = size(X0);
% Grab first puppet switch times and scales
Modes = PLAY{1};
[taus, alphas] = extract_nominal(Modes);

% initialize cost functions
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

% Perform forward integration/simulation
[Puppet_Sol, J_curr] = forward(X0(1:10, 1), Modes, taus, alphas, Costs, Rho);

% Plot the angle solutions
Q = [];
T = [];
% Collect the angle trajectories and time
for i = 1 : length(Puppet_Sol)
    Q = [ Q Puppet_Sol{i}.y(4:9,:) ];
    T = [ T Puppet_Sol{i}.x];
end

generatePuppetGraphs(T, Q);

% Movie generation
% extract hand and knee positions from data
R = position_vectors(Q);
export = 0;
% insert file name between quotes (if you want to save):
makePuppetMovie(R, export, '');