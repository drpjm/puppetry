function [ T, X, L, MU, taus, J ] = puppetSimulation(t0, x0, Modes, Rho, P, dt, puppetId)
% Function that performs COV optimization for a multimode play for one
% puppet.
%
%   INPUT:
%
%   OUTPUT:
%

% load plotting data and params
eval('puppet_data');
L_func = @(x, r)        (x - r)'*P*(x - r);

[X, T, J] = forwardSimulation(dt, t0, x0, Rho, Modes, params, L_func);

% assemble MU init conditions
dCdAlpha = ones(1,length(Modes));
for k = 1 : length(Modes)
    dCdAlpha(k) = alpha_penalties(puppetId)*2*Modes{k}.alpha;
end

% initialize lambda with dPsi_dx
r = zeros(9,1);
r(1:2) = Rho(:, Modes{length(Modes)}.region);
x1f = X(:,length(T));
cost_vec = zeros(1,9);
cost_vec(1,1) = 1;
cost_vec(1,2) = 1;
lam0 = dPsidx(cost_vec, x1f, r);

[L, MU, taus] = backwardSimulation(dt, lam0, Rho, dCdAlpha, X, T, Modes);
% tau_out = [];
% alpha_out = [];
% for j = 1 : length(Modes)
%     tau_out = [tau_out Modes{j}.tau];
%     alpha_out = [alpha_out Modes{j}.alpha];
% end

%% END

% R = evaluateLimbKinematics(X, params);

% Z = {};
% Z{1} = X(1:2,:);
% Z{2} = X2(1:2,:);

% generatePuppetGraphs(T, X(4:9,:));