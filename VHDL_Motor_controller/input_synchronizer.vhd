Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity input_synchronizer is
  port (
    clk : in std_logic; --100MHz
    reset : in std_logic;
    sa : in std_logic;
    sb : in std_logic;
    sa_synch : out std_logic;
    sb_synch : out std_logic
  );
end entity;

architecture rtl of input_synchronizer is
  signal sa_im : std_logic := '0';
  signal sb_im : std_logic := '0';
begin

  process (clk, reset) is
  begin
    if (reset = '1') then
      sa_im <= '0';
      sb_im <= '0';
      sa_synch <= '0';
      sb_synch <= '0';
    elsif rising_edge(clk) then
      sa_im <= sa;
      sb_im <= sb;
      sa_synch <= sa_im;
      sb_synch <= sb_im;
    end if;
  end process;

end architecture;