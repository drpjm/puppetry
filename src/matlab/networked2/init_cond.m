% Initial conditions
t_0 = 0;

% Puppet 1
z01 = [0.55 0.30 0]';
q01 = [0 0 0 0 0 0]';
% adds a time variable initialized to t_0
x01 = [z01; q01; t_0];
% NOTE: state will have 1 more element than necessary!

% Puppet 2
z02 = [0.40 1.40 0]';
q02 = [0 0 0 0 0 0]';
x02 = [z02; q02; t_0];

% Puppet 3
z03 = [1.30 0.80 0]';
q03 = [0 0 0 0 0 0]';
x03 = [z03; q03; t_0];


X0 = [x01 x02 x03];
