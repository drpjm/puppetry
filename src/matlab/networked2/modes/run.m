function f = run(x, t, alpha, motorModel, planar_model, time_dyn)

s = x(length(x));

% motion in plane
z = planar_model(x,0);

% x1 = alpha*v*sin(x(3));
% x2 = alpha*v*cos(x(3));
% x3 = 0;  

%% create control for joints
degToRad = @(a) a*(pi/180);

walk_w_max = degToRad(180); %60 deg/sec
walk_range = [degToRad(0) degToRad(90)];
maxAngle = walk_range(2);
minAngle = walk_range(1);
walk_period = 2*(maxAngle-minAngle)/(alpha*walk_w_max);

% arms do not rotate
u_theta_r = 0;
u_theta_l = 0;

% arms lift depending on length of walk; start off with right arm lead
% arms move out of phase by half a period
% also, left leg starts out to match arm
if(s <  walk_period / 2)
    u_phi_r = motorModel(0,alpha,walk_w_max,s,walk_range);
    u_phi_l = 0;
    
    u_psi_l = motorModel(0,alpha,walk_w_max,s,walk_range);
    u_psi_r = 0;
else

    u_phi_r = motorModel(0,alpha,walk_w_max,s,walk_range);
    u_psi_l = motorModel(0,alpha,walk_w_max,s,walk_range);

    offset_t = s - walk_period / 2;
    % make left motor out of phase
    u_phi_l = motorModel(0,alpha,walk_w_max,offset_t,walk_range);
    %make right leg motor out of phase
    u_psi_r = motorModel(0,alpha,walk_w_max,offset_t,walk_range);
end


u = [u_theta_r u_phi_r u_theta_l u_phi_l u_psi_r u_psi_l]';

f = [z(1); z(2); z(3); u; time_dyn];