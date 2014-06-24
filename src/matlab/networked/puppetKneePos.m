function r = puppetKneePos(side, psi, puppet_params)
%
% Function that calculates the position vector
% of the endpoint of the puppet knee.
%   side = parameter to specify left (1) or right (0) knee
%   q = current configuration
%

w = puppet_params.hip_width;
ratio = puppet_params.l_motor / puppet_params.l_b;

if(side == 0)
    offset = -w/2;
else
    offset = w/2;
end

% psi = ratio*psi;

% includes gear ratio
r = [ puppet_params.l_b*sin(psi);
    offset;
    -puppet_params.l_b*cos(psi) - puppet_params.z_offset];