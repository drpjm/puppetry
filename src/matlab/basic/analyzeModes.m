function [T_total, num_modes, tau_indices] = analyzeModes(modes, taus, dt)
%
% Function that takes a set of modes and outputs the total time and the
% number of modes prescribed.
% modes - cell array of mode functions
% taus - initial array of switch times
%
num_modes = length(modes);

T = 0;
tau_indices = [];

%add up all switch times for total
for i = 1 : length(taus)
    tau_indices = [tau_indices round((taus(i) + T) / dt)];
    T = taus(i) + T;
end

T_total = T;