function r = puppetHandPos(side, theta, phi, puppet_params)
%
% Function that calculates the position vector
% of the endpoint of the puppet arm.
%   side = parameter to specify left (1) or right (0) hand
%   q = current configuration
%
degToRad = @(a) a*(pi/180);

phi_0 = degToRad(puppet_params.phi_0);
l_a = puppet_params.l_a;
w = puppet_params.shoulder_width;

ratio = puppet_params.l_motor / l_a;

if(side == 0)
    offset = -w/2;
else
    offset = w/2;
end

%phi = ratio*phi;

r = [l_a*sin(phi + phi_0)*cos(theta); 
     -l_a*sin(phi + phi_0)*sin(theta) + offset;
     -l_a*cos(phi + phi_0)]; 