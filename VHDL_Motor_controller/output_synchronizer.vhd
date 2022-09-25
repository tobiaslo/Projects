Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity output_synchronizer is
  port (
    clk : in std_logic; --100MHz
    reset : in std_logic;
    dir : in std_logic;
    en : in std_logic;
    dir_synch : out std_logic;
    en_synch : out std_logic
  );
end entity;

architecture rtl of output_synchronizer is 
begin

  process (clk, reset) is
  begin
    if (reset = '1') then
      dir_synch <= '0';
      en_synch <= '0';
    elsif rising_edge(clk) then
      dir_synch <= dir;
      en_synch <= en;
    end if;
  end process;

end architecture;
