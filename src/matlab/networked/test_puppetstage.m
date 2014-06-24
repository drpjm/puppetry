function test_puppetstage()
% this function simply tests how to feed puppet trajectories into the
% display function.
close all;

dim = [2 2];
n = 2;
dt = 0.1;

uni_system =  @(v,th,u) [v*cos(th) v*sin(th) u]';

% unicycle evolution
X = [];
T = 0:dt:10;
tau = 7.5;
for j = 1 : n
    x0 = [50 50 0]';
    % random x0 initializer
    r = rand(1);
    x0 = x0 + [10*r 10*r r]';
    X = [X x0];
    v = 20*rand(1);
%     u1 = rand(1)/2;
    u1 = 1;
%    u2 = -rand(1)/4;
    u2 = -1;
    
    for i = 1 : length(T)-1
        x_curr = X(:,i);
        if (i < floor(tau/dt))
            eval = uni_system(v,x_curr(3),u1);
        else
            eval = uni_system(v,x_curr(3),u2);
        end
        x_next = x_curr + dt.*eval;

        X = [X x_next];
    end
    x1 = X(1,:);
    x2 = X(2,:);
    Z{j} = [x1;  x2];
    X = [];
end

% generate trajectories for all puppets
% T = 0:dt:100;
% for i = 1 : n
%     r = rand(1);
%     z1 = 7*sin(r*T)+cos(r*T)+21;
%     z2 = r*T+T;
%     Z{i} = [z1; z2];
% end

display_stage(Z, n, dim)