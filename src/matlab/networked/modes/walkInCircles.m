function f = walkInCircles(x, t, alpha, motorModel, v, gamma)

x3 = x(3);

%% create control for motion in plane
x1 = alpha*v*sin(x3);
x2 = alpha*v*cos(x3);
x3 = gamma;  

%% create control for joints

degToRad = @(a) a*(pi/180);

% q = x(4:9);

% phi_r = q(2);
% phi_l = q(4);

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
if(t <  walk_period / 2)
    dp_r = motorModel(0,alpha,walk_w_max,t,walk_range);
    dp_l = 0;
    
    ds_l = motorModel(0,alpha,walk_w_max,t,walk_range);
    ds_r = 0;
else

    dp_r = motorModel(0,alpha,walk_w_max,t,walk_range);
    ds_l = motorModel(0,alpha,walk_w_max,t,walk_range);

    offset_t = t - walk_period / 2;
    % make left motor out of phase
    dp_l = motorModel(0,alpha,walk_w_max,offset_t,walk_range);
    %make right leg motor out of phase
    ds_r = motorModel(0,alpha,walk_w_max,offset_t,walk_range);
end

u = [dt_r dp_r dt_l dp_l ds_r ds_l]';

f = [x1; x2; x3; u];
