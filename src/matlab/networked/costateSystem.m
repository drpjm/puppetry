function ret = costateSystem(x, l, r, dfdx)
%
% This function returns the current value of the costate.
% x - state
% l - costate
% r - reference
% dfdx - value of the derivative of f (i.e. current mode)
persistent DF

DF = zeros(length(x));
DF(1,3) = dfdx(1);
DF(2,3) = dfdx(2);

ret = -dLdx(x,r) -l'*DF;
ret = ret';