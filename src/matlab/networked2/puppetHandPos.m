function r = puppetHandPos(side,width,theta,phi)
%
% Function that calculates the position vector
% of the endpoint of the puppet arm.
%   side = parameter to specify left (1) or right (0) hand
%   q = current configuration
%
l_a = 0.08; %8 cm lengh of arm
l_motor = 0.04; %length of lifting actuator
w = width;   %6 cm shoulder width
phi_0 = 10; %10 deg

degToRad = @(a) a*(pi/180);

ratio = l_motor/l_a;

if(side == 0)
    offset = -w/2;
else
    offset = w/2;
end

%phi = ratio*phi;

% includes gear ratio
% r = [l_a*sin(degToRad(phi + phi_0))*cos(degToRad(theta)); 
%      -l_a*sin(degToRad(phi + phi_0))*sin(degToRad(theta)) + offset;
%      -l_a*cos(degToRad(phi + phi_0))];
 
r = [l_a*sin(phi + degToRad(phi_0))*cos(theta); 
     -l_a*sin(phi + degToRad(phi_0))*sin(theta) + offset;
     -l_a*cos(phi + degToRad(phi_0))]; 