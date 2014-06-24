function ret = dPsidx(weights, x, r)
%   weights - array of weights for the matrix
%   x - current state
%   r - reference trajectory

Q = diag(weights);

ret = 2.*(x - r)'*Q;
ret = ret';