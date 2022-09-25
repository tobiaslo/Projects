Library IEEE;
use IEEE.std_logic_1164.all;

Library work;
use work.bin2ssd_pck.all;

entity bin2ssd is
  port
  (
    bin : in std_logic_vector(3 downto 0);
    abcdefg : out std_logic_vector(6 downto 0)
  );
end bin2ssd;

architecture rtl of bin2ssd is

begin
  abcdefg <= bin2ssd_hex(bin);
end architecture;



