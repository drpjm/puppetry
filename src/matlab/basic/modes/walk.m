function u = walk(alpha, t, q, motorModel)
%
% This function abstracts the 'walk' mode of the puppet.
% alpha - scaling value on (0,1)
% t - current time instant
% q - current state
%

degToRad = @(a) a*(pi/180);

phi_r = q(2);
phi_l = q(4);
psi_r = q(5);
psi_l = q(6);


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
    dp_r = motorModel(0,alpha,walk_w_max,t,1,walk_range);
    dp_l = 0;
    
    ds_l = motorModel(0,alpha,walk_w_max,t,1,walk_range);
    ds_r = 0;
else

    dp_r = motorModel(0,alpha,walk_w_max,t,1,walk_range);
    ds_l = motorModel(0,alpha,walk_w_max,t,1,walk_range);

    offset_t = t - walk_period / 2;
    % make left motor out of phase
    dp_l = motorModel(0,alpha,walk_w_max,offset_t,1,walk_range);
    %make right leg motor out of phase
    ds_r = motorModel(0,alpha,walk_w_max,offset_t,1,walk_range);
end

%check against current phi_r/phi_l
if((phi_r >= maxAngle && dp_r > 0) || (phi_r <= minAngle && dp_r < 0))
    dp_r = 0;
end
if((phi_l >= maxAngle && dp_l > 0) || (phi_l <= minAngle && dp_l < 0))
    dp_l = 0;
end


u = [dt_r dp_r dt_l dp_l ds_r ds_l]';