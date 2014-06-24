function ret = muSystem(x, l, mu, f)
% Function that calculates the mu costate associated with alpha costs.
% x - state
% l - lambda costate
% mu - mu costate
% f - vector of mode's dfdalpha

ret = l'*f;