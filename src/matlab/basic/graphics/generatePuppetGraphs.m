function generatePuppetGraphs(T,Q,R)
%
% Function produces graphs related to puppet configuration.
%

Theta_r = Q(1,:);
Phi_r = Q(2,:);
Theta_l = Q(3,:);
Phi_l = Q(4,:);
Psi_r = Q(5,:);
Psi_l = Q(6,:);

R_right = R{1};
R_left = R{2};
R_right_leg = R{3};
R_left_leg = R{4};

%Configuration graphs
subplot(2,1,1);
plot(T,Theta_r,T,Phi_r,T,Psi_r);
title('Right Joint Angles');
ylabel('Angle, deg');
xlabel('t, sec');
%legend('\theta', '\phi', '\psi - leg lift');
subplot(2,1,2);
plot(T,Theta_l,T,Phi_l,T,Psi_l);
title('Left Joint Angles');
ylabel('Angle, deg');
xlabel('t, sec');
%legend('\theta', '\phi', '\psi - leg lift');

% Trajectory graphs
% figure;
% subplot(2,1,1);
% plot(T,R_right(1,:),'r',T,R_right(2,:),'b',T,R_right(3,:),'g');
% xlabel('t, sec');
% ylabel('position, m');
% legend('x(t)','y(t)', 'z(t)');
% title('Limb Positions - Right');
% subplot(2,1,2);
% plot(T,R_left(1,:),'r',T,R_left(2,:),'b',T,R_left(3,:),'g');
% xlabel('t, sec');
% ylabel('position, m');
% legend('x(t)','y(t)', 'z(t)');
% title('Limb Positions - Left');
