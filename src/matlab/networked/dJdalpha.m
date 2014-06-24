function ret = dJdalpha(MU)
% Function that calculates dJdalpha for each mode
%
% MU - mu costate
% Modes - puppet play modes

ret = [];

for i = 1 : length(MU)    
    dJdalpha_i = MU{i}(1);
    ret = [ret dJdalpha_i];    
end