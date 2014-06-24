function R = evaluateLimbKinematics(X, puppet_params)

r_init_right = puppetHandPos(0,0,0,puppet_params);
r_init_left = puppetHandPos(1,0,0,puppet_params);
rleg_init_right = puppetKneePos(0,0,puppet_params);
rleg_init_left = puppetKneePos(1,0,puppet_params);

R = cell(1,4);
R_right = [];
R_left = [];
R_right_leg = [];
R_left_leg = [];

% extract joint angles from puppet state!
Q = X(4:9,:);


for i = 1 : length(Q)
    
    q = Q(:, i);

    % evaluate arm kinematics
    r_right = puppetHandPos(0,q(1),q(2), puppet_params);
    R_right = [R_right r_right];
    r_left = puppetHandPos(1,q(3),q(4), puppet_params);
    R_left = [R_left r_left];

    % evaluate leg kinematics
    rleg_right = puppetKneePos(0,q(5),puppet_params);
    R_right_leg = [R_right_leg rleg_right];
    rleg_left = puppetKneePos(1,q(6),puppet_params);
    R_left_leg = [R_left_leg rleg_left];

end

R = {R_right R_left R_right_leg R_left_leg};
