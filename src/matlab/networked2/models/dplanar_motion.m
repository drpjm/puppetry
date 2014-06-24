function ret = dplanar_motion(x, v, gamma, alpha)

%% create control for motion in plane
x1 = v*sin(x(3));
x2 = v*cos(x(3));
x3 = 0;

ret = [x1 x2 x3];

end