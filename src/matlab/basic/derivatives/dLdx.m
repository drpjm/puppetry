function ret = dLdx(x)
%
% Calculates the value of the d/dx of the integrand of the cost functional.
%
%   x - current state
n = length(x);
P = zeros(n);
P(1,1) = 1;
P(2,2) = 1;
P(3,3) = 1;
P(4,4) = 1;
%P = eye(n);

ret = 2.*x'*P;