function R = position_vectors(Q)
% A function that accepts the joint angles and generates vectors for
% the makePuppetMovie function


theta_r = Q(1,:);
phi_r = Q(2,:);
theta_l = Q(3,:);
phi_l = Q(4,:);
psi_r = Q(5,:);
psi_l = Q(6,:);


shoulder_width = 0.07;
hip_width = 0.06;

R_right_arm = zeros(3,length(Q));
R_left_arm = zeros(3,length(Q));
R_right_leg= zeros(3,length(Q));
R_left_leg = zeros(3,length(Q));

for i = 1:  length(Q)

    % arm kinematics
    r_right = puppetHandPos( 0, shoulder_width, theta_r(i), phi_r(i) );
    R_right_arm(:,i) = r_right;

    r_left = puppetHandPos( 1,shoulder_width, theta_l(i), phi_l(i) );
    R_left_arm(:,i) = r_left;

    % evaluate leg kinematics
    rleg_right = puppetKneePos( 0,hip_width, psi_r(i) );
    R_right_leg(:,i) = rleg_right;
    
    rleg_left = puppetKneePos( 1,hip_width, psi_l(i) );
    R_left_leg(:,i) = rleg_left;

end

R = {R_right_arm R_left_arm R_right_leg R_left_leg};
