function ret = Lfunc(x,t)
% Function that returns the integrand cost value.

%12/22/07 Cost weights the values of Theta_r and Theta_l
n = length(x);
P = zeros(n);
P(1,1) = 1;
P(2,2) = 1;
P(3,3) = 1;
P(4,4) = 1;
%P = eye(n);

ret = x'*P*x;