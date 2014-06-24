function ret = dJda(C_SOLS, taus)

num = length(C_SOLS);
ret = zeros(1,num);

for j = 1 : num
    
    tau_j = C_SOLS{j}.x(end);
    cs = deval(C_SOLS{j}, tau_j);
    ret(j) = cs(end);
    
end
% for j = num : -1 : 1
%     t = T - sum(taus(j:num));
%     cs = deval(C_SOLS{j}, t);
%     ret(j) = cs(length(cs));
% end



end