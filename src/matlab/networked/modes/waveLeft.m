function f = waveLeft(x, t, alpha, motorModel, v, gamma)


x3 = x(3);
%% create control for motion in plane
x1 = alpha*v*sin(x3);
x2 = alpha*v*cos(x3);
x3 = gamma;  


%% create control for joints
degToRad = @(a) a*(pi/180);

% q = x(4:9);

% theta_l = q(3);
% phi_l = q(4);

lift_speed = degToRad(70);
lift_range = [degToRad(0) degToRad(90)];

rotate_speed = degToRad(45);
rotate_range = [degToRad(0) degToRad(45)];

maxLiftAngle = lift_range(2);
minLiftAngle = lift_range(1);
maxRotateAngle = rotate_range(2);
minRotateAngle = rotate_range(1);


lift_period = (maxLiftAngle-minLiftAngle)/(alpha*lift_speed);
rotate_period = (maxRotateAngle-minRotateAngle)/(alpha*rotate_speed);

% right arm does not move
u_theta_r = 0;
u_phi_r = 0;

% left arm wave motions
if(t < lift_period)
    u_theta_l = 0;
    u_phi_l = motorModel(0, alpha, lift_speed, t, lift_range);
else
    
    rotate_t = t - lift_period;
    u_theta_l = motorModel(1,alpha,rotate_speed,rotate_t,rotate_range);
    u_phi_l = 0;
    
    % begin lowereing arm after part of rotation
    if(t > (lift_period + rotate_period))
        
        lower_t = t - (lift_period + rotate_period);
        u_phi_l = motorModel(0,alpha,-lift_speed,lower_t,lift_range);
        
    end
    
end

u = [u_theta_r u_phi_r u_theta_l u_phi_l 0 0]';

f = [x1; x2; x3; u];