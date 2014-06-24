function u = motor(side, alpha, w_max, t, repeat, angleRange)
%
% Control signal that characterizes the open loop control of a
% puppet motor.
%
% alpha - scaling
% w_max - motors max angular speed
%

period = 2*(angleRange(2)-angleRange(1))/(alpha*abs(w_max));
f = 1/period;

if repeat
    %basic square wave
    %u = alpha*w_max*square(2*pi*f*t);
    %square wave approximation
    u = alpha*w_max*(4/pi)*(sin(2*pi*f*t) + (1/3)*sin(6*pi*f*t));
else
    if(t < period/2)
        u = alpha*w_max;
    else
        u = 0;
    end
end

if side
    u = -1*u;
end