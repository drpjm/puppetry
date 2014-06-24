function ret = dfdx(x, t, alpha, v)

persistent M

M = zeros(length(x));
M(1,3) = alpha*v*cos(x(3));
M(2,3) = -alpha*v*sin(x(3));

ret = M;

end