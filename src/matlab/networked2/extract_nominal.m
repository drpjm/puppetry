function [taus,alphas] = extract_nominal(Modes)

taus = zeros(1,length(Modes));
alphas = zeros(1,length(Modes));

for j = 1 : length(Modes)
    taus(j) = Modes{j}.tau;
    alphas(j) = Modes{j}.alpha;
end