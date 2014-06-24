function r = puppetKneePos(side,width,psi)
%
% Function that calculates the position vector
% of the endpoint of the puppet knee.
%   side = parameter to specify left (1) or right (0) knee
%   q = current configuration
%
l_b = 0.07; %7 cm lengh of leg
l_motor = 0.04; %length of lifting actuator
w = width;   %6 cm shoulder width
z_offset = 0.08;

% degToRad = @(a) a*(pi/180);

ratio = l_motor/l_b;

if(side == 0)
    offset = -w/2;
else
    offset = w/2;
end

% psi = ratio*psi;

% includes gear ratio
r = [l_b*sin(psi);
    offset;
    -l_b*cos(psi) - z_offset];