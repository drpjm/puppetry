function ret = costateSystem(x,l,mode)
%
% This function returns the current value of the costate.
%

% 12/21/07 - model of puppet has no dependence on state (x), therefore the
% costate function has no df/dx term!
ret = -dLdx(x);