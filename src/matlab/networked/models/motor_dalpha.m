function u = motor_dalpha(side, alpha, w_max, t, angleRange)
%
% Function that serves as the d/dalpha for backwards simulation of the
% costates.
%
% alpha - scaling
% w_max - motors max angular speed
%

period = 2*(angleRange(2)-angleRange(1))/(alpha*abs(w_max));
f = 1/period;

%square wave approximation
term1 = (4/pi)*w_max*(sin(2*pi*f*t) + (1/3)*sin(6*pi*f*t));
term2 = 8*w_max*f*(cos(2*pi*f*t) + cos(6*pi*f*t));
u =  term1 + term2;

if side
    u = -1*u;
end