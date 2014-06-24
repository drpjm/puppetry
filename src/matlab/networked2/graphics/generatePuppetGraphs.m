function generatePuppetGraphs(T,Q)
%
% Function produces graphs related to puppet configuration.
%
figure(13);

Theta_r = Q(1,:);
Phi_r = Q(2,:);
Theta_l = Q(3,:);
Phi_l = Q(4,:);
Psi_r = Q(5,:);
Psi_l = Q(6,:);

%Configuration graphs
subplot(2,1,1);
plot(T,Theta_r,T,Phi_r,T,Psi_r);
title('Right Joint Angles');
ylabel('Angle, rad');
xlabel('t, sec');
%legend('\theta', '\phi', '\psi - leg lift');
subplot(2,1,2);
plot(T,Theta_l,T,Phi_l,T,Psi_l);
title('Left Joint Angles');
ylabel('Angle, rad');
xlabel('t, sec');
%legend('\theta', '\phi', '\psi - leg lift');