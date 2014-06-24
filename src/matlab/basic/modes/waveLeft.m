function u = waveLeft(alpha, t, q, motorModel)
%
% This function abstracts the 'waveLeft' mode of the puppet.
% alpha - scaling value on (0,1)
% t - current time instant
% q - current state
%

degToRad = @(a) a*(pi/180);

theta_l = q(3);
phi_l = q(4);

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
dt_r = 0;
dp_r = 0;

dp_l = motorModel(0,alpha,lift_speed,t,0,lift_range);

if(t < lift_period)
    dt_l = 0;
else
    rotate_t = t - lift_period;
    dt_l = motorModel(1,alpha,rotate_speed,rotate_t,1, rotate_range);
    % begin lowereing arm after part of rotation
    if(t > (lift_period + 2*rotate_period))
        lower_t = t - (lift_period + 2*rotate_period);
        dp_l = motorModel(0,alpha,-lift_speed,lower_t,0,lift_range);
    end
end

%check current configuration with calculated velocities
if((phi_l > maxLiftAngle && dp_l > 0) || (phi_l <= minLiftAngle && dp_l < 0))
    dp_l = 0;
end

u = [dt_r dp_r dt_l dp_l 0 0]';