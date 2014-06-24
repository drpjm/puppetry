function P = cost_matrix(type)
%
%   Function that returns a special cost matrix based on whether
% the mode is 'standing' or 'moving.'
%
% type  - 0, standing; 1, moving
% P       - 9x9 matrix of cost weights.

if type == 0
    P = diag(ones(1,9));
    P(1,1) = 0;
    P(2,2) = 0;
    P(3,3) = 0;
    P(8,8) = 0;
    P(9,9) = 0;
else
    P = diag(zeros(1,9));
    P(1,1) = 1;
    P(2,2) = 1;
end