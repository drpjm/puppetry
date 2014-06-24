function Q = puppetSystem(u);
%
% Function that encapsulates the entire puppet system (arms and legs). The
% input to the function is simply the current control signals.
%

Q = eye(6)*u;