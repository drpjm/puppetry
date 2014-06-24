function u = motor(side, alpha, w_max, t, angleRange)
%
% Control signal that characterizes the open loop control of a
% puppet motor.
%
% alpha - scaling
% w_max - motors max angular speed
%

period = 2*(angleRange(2)-angleRange(1))/(alpha*abs(w_max));
f = 1/period;

%square wave approximation
u = alpha*w_max*(4/pi)*(sin(2*pi*f*t) + (1/3)*sin(6*pi*f*t));

if side
    u = -1*u;
end