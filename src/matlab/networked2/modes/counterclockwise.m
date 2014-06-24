function f = counterclockwise(x, t, alpha, motor_model, planar_model, time_dyn)

s = x(length(x));

% motion in plane
z = planar_model(x,-1);

% joint motion
degToRad = @(a) a*(pi/180);

walk_w_max = degToRad(40); %40 deg/sec
walk_range = [degToRad(0) degToRad(70)];
maxAngle = walk_range(2);
minAngle = walk_range(1);
walk_period = 2*(maxAngle-minAngle)/(alpha*walk_w_max);

% arms do not rotate
dt_r = 0;
dt_l = 0;

% arms lift depending on length of walk; start off with right arm lead
% arms move out of phase by half a period
% also, left leg starts out to match arm
if(s <  walk_period / 2)
    dp_r = motor_model(0,alpha,walk_w_max,s,walk_range);
    dp_l = 0;
    
    ds_l = motor_model(0,alpha,walk_w_max,s,walk_range);
    ds_r = 0;
else

    dp_r = motor_model(0,alpha,walk_w_max,s,walk_range);
    ds_l = motor_model(0,alpha,walk_w_max,s,walk_range);

    offset_t = s - walk_period / 2;
    % make left motor out of phase
    dp_l = motor_model(0,alpha,walk_w_max,offset_t,walk_range);
    %make right leg motor out of phase
    ds_r = motor_model(0,alpha,walk_w_max,offset_t,walk_range);
end

u = [dt_r dp_r dt_l dp_l ds_r ds_l]';

f = [z(1); z(2); z(3); u; time_dyn];
