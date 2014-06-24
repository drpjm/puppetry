function ret = dLdx(x, r)

%   x - current state
n = length(x);
unity = ones(1,n);
P = diag(unity);
% test
P(1,1) = 5;
P(2,2) = 5;
%
P(3,3) = 0;
P(8,8) = 0;
P(9,9) = 0;


ret = 2.*(x - r)'*P;