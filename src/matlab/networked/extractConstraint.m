function mu = extractConstraint(c, PLAY)
% Function that examines the puppet plat to extract necessary information
% for mu ascent.

puppet_j = c(1,1);
puppet_k = c(1,2);

Modes_j = PLAY{puppet_j};
Modes_k = PLAY{puppet_k};

tau_j = Modes_j{c(2,1)}.tau;
tau_k = Modes_k{c(2,2)}.tau;

mu = tau_j - tau_k;