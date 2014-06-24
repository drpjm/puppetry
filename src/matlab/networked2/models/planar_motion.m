function ret = planar_motion(x, v, gamma, alpha)

%% create control for motion in plane
x1 = alpha*v*sin(x(3));
x2 = alpha*v*cos(x(3));
x3 = gamma;  

ret = [x1 x2 x3];

end